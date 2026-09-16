import { useCallback, useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import logo from "../../../assets/logo.png";
import { useAuth } from "../../../common/hooks/useAuth";
import { getDefaultRouteForRole } from "../../../common/utils/roleAccess";

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [googleError, setGoogleError] = useState("");
  const [googleReady, setGoogleReady] = useState(false);
  const navigate = useNavigate();
  const { signIn, signInWithGoogle, loading, error } = useAuth();
  const googleButtonRef = useRef(null);
  const googleInitializedRef = useRef(false);

  async function handleSubmit(event) {
    event.preventDefault();
    const data = await signIn(email, password);
    if (data) {
      navigate(getDefaultRouteForRole(data.role));
    }
  }

  const handleCredentialResponse = useCallback(
    async (response) => {
      setGoogleError("");
      const data = await signInWithGoogle(response.credential);
      if (data) {
        navigate(getDefaultRouteForRole(data.role));
      } else {
        setGoogleError("This Google account isn't linked to a Fixinh staff account.");
      }
    },
    [signInWithGoogle, navigate]
  );

  // Renders Google's own button into a hidden node once GIS has loaded, so our custom-styled
  // button (below) can forward its click there — that keeps the popup/One Tap credential flow
  // running through GIS's own trusted button instead of a script-only call browsers may block.
  // The script tag in index.html is async/defer, so it may not be ready the instant this effect
  // first runs — poll briefly instead of giving up after a single check.
  useEffect(() => {
    // Guards against React StrictMode's dev-only double-invoke (mount, cleanup, mount again):
    // without this, GIS's own "initialize() called multiple times" warning fires and it can
    // start a second popup attempt with no direct click behind it, which browsers block.
    if (!GOOGLE_CLIENT_ID || googleInitializedRef.current) return;

    let cancelled = false;
    let attempts = 0;
    const MAX_ATTEMPTS = 50; // ~5s at 100ms apart

    function trySetup() {
      if (cancelled || googleInitializedRef.current) return;
      if (!window.google?.accounts?.id || !googleButtonRef.current) {
        attempts += 1;
        if (attempts >= MAX_ATTEMPTS) {
          setGoogleError("Couldn't load Google sign-in. Check your connection and reload the page.");
          return;
        }
        setTimeout(trySetup, 100);
        return;
      }

      window.google.accounts.id.initialize({
        client_id: GOOGLE_CLIENT_ID,
        callback: handleCredentialResponse,
      });
      window.google.accounts.id.renderButton(googleButtonRef.current, {
        type: "standard",
        theme: "outline",
        size: "large",
      });
      googleInitializedRef.current = true;
      setGoogleReady(true);
    }

    trySetup();
    return () => {
      cancelled = true;
    };
  }, [handleCredentialResponse]);

  function handleGoogleSignIn() {
    if (!GOOGLE_CLIENT_ID) {
      setGoogleError("Google sign-in is not configured for this deployment.");
      return;
    }
    const hiddenButton = googleButtonRef.current?.querySelector('div[role="button"]');
    if (hiddenButton) {
      hiddenButton.click();
    } else if (!googleReady) {
      setGoogleError("Google sign-in is still loading — please wait a moment and try again.");
    } else {
      setGoogleError("Google sign-in isn't available right now.");
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-50 px-4 dark:bg-slate-950">
      <div className="w-full max-w-sm">
        <div className="mb-8 flex flex-col items-center gap-3">
          <img src={logo} alt="Fixinh" className="h-11 w-11 rounded-lg object-contain" />
          <div className="text-center">
            <h1 className="text-xl font-bold text-slate-900 dark:text-white">Welcome back</h1>
            <p className="text-sm text-slate-500 dark:text-slate-400">
              Sign in to your Fixinh account
            </p>
          </div>
        </div>

        <form
          onSubmit={handleSubmit}
          className="rounded-xl bg-white p-6 shadow-sm dark:bg-slate-900"
        >
          <div className="mb-4">
            <label
              htmlFor="email"
              className="mb-1.5 block text-sm font-medium text-slate-700 dark:text-slate-300"
            >
              Email
            </label>
            <input
              id="email"
              type="email"
              required
              autoComplete="email"
              placeholder="name@company.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 placeholder-slate-400 focus:border-blue-400 focus:outline-none dark:border-slate-700 dark:bg-slate-800 dark:text-slate-200"
            />
          </div>

          <div className="mb-4">
            <div className="mb-1.5 flex items-center justify-between">
              <label
                htmlFor="password"
                className="text-sm font-medium text-slate-700 dark:text-slate-300"
              >
                Password
              </label>
            </div>
            <input
              id="password"
              type="password"
              required
              autoComplete="current-password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 placeholder-slate-400 focus:border-blue-400 focus:outline-none dark:border-slate-700 dark:bg-slate-800 dark:text-slate-200"
            />
          </div>

          {(error || googleError) && (
            <p className="mb-4 text-sm text-red-600" role="alert">
              {error || googleError}
            </p>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-lg bg-blue-600 py-2.5 text-sm font-medium text-white transition-colors hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {loading ? "Signing in…" : "Sign in"}
          </button>

          <div className="my-5 flex items-center gap-3">
            <div className="h-px flex-1 bg-slate-200 dark:bg-slate-700" />
            <span className="text-xs text-slate-400">Or continue with</span>
            <div className="h-px flex-1 bg-slate-200 dark:bg-slate-700" />
          </div>

          {/* Real Google button, rendered off-screen by GIS — our styled button below forwards
              its click here so the visuals stay consistent with the rest of the form. */}
          <div ref={googleButtonRef} className="hidden" />

          <button
            type="button"
            onClick={handleGoogleSignIn}
            className="flex w-full items-center justify-center gap-2.5 rounded-lg border border-slate-200 bg-white py-2.5 text-sm font-medium text-slate-700 transition-colors hover:bg-slate-50 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-200 dark:hover:bg-slate-700"
          >
            <GoogleIcon />
            Sign in with Google
          </button>
        </form>

        <p className="mt-6 text-center text-xs text-slate-400">
          Fixinh · Service Management Portal
        </p>
      </div>
    </div>
  );
}

function GoogleIcon() {
  return (
    <svg viewBox="0 0 24 24" className="h-4 w-4" aria-hidden="true">
      <path
        fill="#4285F4"
        d="M23.5 12.27c0-.79-.07-1.54-.2-2.27H12v4.51h6.47a5.6 5.6 0 01-2.4 3.65v3h3.86c2.26-2.09 3.57-5.17 3.57-8.89z"
      />
      <path
        fill="#34A853"
        d="M12 24c3.24 0 5.95-1.07 7.93-2.9l-3.86-3c-1.07.72-2.45 1.14-4.07 1.14-3.13 0-5.78-2.11-6.73-4.96H1.28v3.11A12 12 0 0012 24z"
      />
      <path
        fill="#FBBC05"
        d="M5.27 14.28A7.2 7.2 0 014.9 12c0-.79.14-1.56.37-2.28V6.61H1.28A12 12 0 000 12c0 1.94.46 3.77 1.28 5.39z"
      />
      <path
        fill="#EA4335"
        d="M12 4.75c1.77 0 3.35.61 4.6 1.8l3.42-3.42C17.95 1.19 15.24 0 12 0A12 12 0 001.28 6.61l3.99 3.11C6.22 6.86 8.87 4.75 12 4.75z"
      />
    </svg>
  );
}
