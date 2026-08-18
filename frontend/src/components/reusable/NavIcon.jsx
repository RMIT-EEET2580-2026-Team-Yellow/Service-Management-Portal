const paths = {
  grid: "M4 4h7v7H4zM13 4h7v7h-7zM4 13h7v7H4zM13 13h7v7h-7z",
  calendar: "M4 5h16v16H4zM4 9h16M8 3v4M16 3v4",
  users: "M9 11a3 3 0 100-6 3 3 0 000 6zM17 11a3 3 0 100-6 3 3 0 000 6zM3 21v-1a5 5 0 015-5h2a5 5 0 015 5v1M14 15h1a5 5 0 015 5v1",
  bell: "M12 3a5 5 0 00-5 5v3.5c0 .8-.3 1.6-.9 2.2L4.5 15.5h15l-1.6-1.8a3 3 0 01-.9-2.2V8a5 5 0 00-5-5zM9.5 19a2.5 2.5 0 005 0",
  sun: "M12 4V2M12 22v-2M4 12H2M22 12h-2M5 5l-1.4-1.4M19 19l-1.4-1.4M5 19l-1.4 1.4M19 5l-1.4 1.4M12 17a5 5 0 100-10 5 5 0 000 10z",
  moon: "M20 14.5A8 8 0 019.5 4 8 8 0 1020 14.5z",
  logout: "M9 3H5a2 2 0 00-2 2v14a2 2 0 002 2h4M16 17l5-5-5-5M21 12H9",
  chevron: "M6 9l6 6 6-6",
  check: "M5 13l4 4L19 7",
  wrench: "M14.7 6.3a4 4 0 10-5.4 5.4L3 18v3h3l6.3-6.3a4 4 0 005.4-5.4l-2.6 2.6-2-2z",
  chart: "M4 20V10M10 20V4M16 20v-7M4 20h16",
};

export default function NavIcon({ name, className = "h-5 w-5" }) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" className={className}>
      <path d={paths[name]} />
    </svg>
  );
}
