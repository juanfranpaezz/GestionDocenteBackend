export interface GroupedAverage {
  evaluationTypeId: number;
  evaluationTypeName: string;
  average: number;
  evaluationsCount: number;
  evaluationIds: number[];
}

export interface StudentGroupedAverages {
  studentId: number;
  firstName: string;
  lastName: string | null;
  groupedAverages: GroupedAverage[];
  finalAverage: number | null;
}

