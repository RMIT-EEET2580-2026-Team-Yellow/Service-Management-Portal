import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";
import fs from "node:fs";
import path from "node:path";

// HTTPS for the frontend's own dev server (A.2.5/A.2.d) - self-signed cert, generated via:
//   openssl req -x509 -newkey rsa:2048 -nodes -keyout certs/localhost-key.pem
//     -out certs/localhost-cert.pem -days 3650 -subj "/CN=localhost/OU=SMT/O=Fixinh/L=HoChiMinh/ST=HoChiMinh/C=VN"
//     -addext "subjectAltName=DNS:localhost,IP:127.0.0.1"
const certDir = path.resolve(__dirname, "certs");

export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
  ],
  server: {
    https: {
      key: fs.readFileSync(path.join(certDir, "localhost-key.pem")),
      cert: fs.readFileSync(path.join(certDir, "localhost-cert.pem")),
    },
  },
});