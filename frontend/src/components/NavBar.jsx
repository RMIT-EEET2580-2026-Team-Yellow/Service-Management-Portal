import { NavLink } from "react-router-dom";

function Navbar() {
  const navItems = [
    { name: "Login", path: "/" },
    { name: "Bookings", path: "/bookings" },
    { name: "Staff", path: "/staff" },
    { name: "Technician", path: "/technician" },
    { name: "Statistics", path: "/statistics" },
  ];

  return (
    <nav className="bg-slate-900 shadow-md">
      <div className="max-w-7xl mx-auto flex items-center justify-between px-8 h-16">
        {/* Logo */}
        <div className="text-xl font-bold text-white tracking-wide">
          SMT Portal
        </div>

        {/* Navigation */}
        <div className="flex items-center gap-2">
          {navItems.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `px-4 py-2 rounded-lg font-medium transition-all duration-200 ${
                  isActive
                    ? "bg-blue-600 text-white"
                    : "text-gray-300 hover:bg-slate-700 hover:text-white"
                }`
              }
            >
              {item.name}
            </NavLink>
          ))}
        </div>
      </div>
    </nav>
  );
}

export default Navbar;