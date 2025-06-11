import React, { ReactNode } from "react";
import Navbar from "./NavBar";
import Footer from "./Footer";

interface LayoutProps {
  children: ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <div className="pt-36 flex-1">
        <main className="container mx-auto px-4 flex-grow">{children}</main>
      </div>
      <Footer />
    </div>
  );
};

export default Layout;
