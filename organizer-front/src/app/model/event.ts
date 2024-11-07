import { File } from "./file";

export interface EventFile extends File{
    startDate?: string,
    endDate?: string,
    location?: string,
    eventDates: number[]
}
