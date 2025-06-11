"use client";
import React, { useState } from "react";
import SelectionBar from "./SelectionBar";
import EnrollmentChart from "./EnrollmentChart";

export default function SelectionBarWithChart() {
  const [selected, setSelected] = useState<{ dept: string; course: string } | null>(null);

  const handleAdd = (dept: string, course: string) => {
    setSelected({ dept, course });
    // You can pass this to EnrollmentChart as props if needed
    console.log(dept, course);
  };

  return (
    <>
      <SelectionBar onAdd={handleAdd} />
      <EnrollmentChart />
    </>
  );
}