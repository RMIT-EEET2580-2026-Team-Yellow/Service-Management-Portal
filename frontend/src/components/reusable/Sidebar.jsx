import { useEffect, useRef, useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import logo from "../../assets/logo.png";
import NavIcon from "./NavIcon";
import { useAuth } from "../../common/hooks/useAuth";
import { ROUTE_ROLES } from "../../common/utils/roleAccess";

const navItems = [
  { icon: "grid", label: "Dashboard", path: "/dashboard" },
  { icon: "calendar", label: "Bookings", path: "/bookings" },
  { icon: "users", label: "Staff", path: "/staff" },
  { icon: "wrench", label: "Technician", path: "/technician" },
  { icon: "chart", label: "Statistics", path: "/statistics" },
  { icon: "bell", label: "Notifications", path: "/notifications" },
];

export default function Sidebar() {
  const [isDark, setIsDark] = useState(
    () => document.documentElement.classList.contains("dark")
  );
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef(null);
  const navigate = useNavigate();
  const { user, signOut } = useAuth();
  const visibleNavItems = navItems.filter((item) =>
    ROUTE_ROLES[item.path]?.includes(user?.role)
  );

  useEffect(() => {
    document.documentElement.classList.toggle("dark", isDark);
  }, [isDark]);

  useEffect(() => {
    function handleClickOutside(event) {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setMenuOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <aside className="sticky top-0 flex h-screen w-16 flex-shrink-0 flex-col items-center gap-2 border-r border-slate-200 bg-slate-900 py-4 dark:border-slate-800">
      <img src={logo} alt="Fixinh" className="mb-4 h-9 w-9 rounded-lg object-contain" />
      {visibleNavItems.map((item) => (
        <NavLink
          key={item.path}
          to={item.path}
          aria-label={item.label}
          className={({ isActive }) =>
            `flex h-10 w-10 items-center justify-center rounded-lg text-lg transition-colors ${
              isActive
                ? "bg-blue-600/20 text-blue-400"
                : "text-slate-400 hover:bg-slate-800 hover:text-white"
            }`
          }
        >
          <NavIcon name={item.icon} />
        </NavLink>
      ))}

      <div ref={menuRef} className="relative mt-auto">
        {menuOpen && (
          <div className="absolute bottom-0 left-14 z-10 w-48 rounded-xl border border-slate-700 bg-slate-800 p-1.5 shadow-lg">
            <div className="px-2.5 py-2">
              <p className="text-sm font-medium text-white">{user?.staffId ?? "Unknown"}</p>
              <p className="text-xs text-slate-400">{user?.role ?? ""}</p>
            </div>
            <div className="my-1 h-px bg-slate-700" />
            <button
              onClick={() => setIsDark((v) => !v)}
              className="flex w-full items-center gap-2.5 rounded-lg px-2.5 py-2 text-left text-sm text-slate-200 hover:bg-slate-700"
            >
              <NavIcon name={isDark ? "sun" : "moon"} />
              {isDark ? "Switch to light mode" : "Switch to dark mode"}
            </button>
            <button
              onClick={async () => {
                setMenuOpen(false);
                await signOut();
                navigate("/");
              }}
              className="flex w-full items-center gap-2.5 rounded-lg px-2.5 py-2 text-left text-sm text-red-400 hover:bg-slate-700"
            >
              <NavIcon name="logout" />
              Log out
            </button>
          </div>
        )}
        <button
          aria-label="Account menu"
          onClick={() => setMenuOpen((v) => !v)}
          className={`flex h-9 w-9 items-center justify-center rounded-full text-xs font-semibold transition-colors ${
            menuOpen ? "bg-blue-500 text-white" : "bg-blue-100 text-blue-700 hover:bg-blue-200"
          }`}
        >
          {user?.role ? user.role.slice(0, 2) : "?"}
        </button>
      </div>
    </aside>
  );
}
