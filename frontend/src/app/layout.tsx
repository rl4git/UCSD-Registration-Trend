import React from "react";
import type { Metadata } from "next";
import { GeistSans } from "geist/font/sans";
import "./globals.css";

export const metadata: Metadata = {
  title: "UCSD Course Enrollment Trends",
  description: "Visualize UCSD course enrollment trends over time",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className={`${GeistSans.className} antialiased dark:bg-gray-950`}>
      <body>{children}</body>
    </html>
  );
} 