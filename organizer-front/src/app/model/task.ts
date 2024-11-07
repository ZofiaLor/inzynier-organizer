import { File } from "./file";

export interface TaskFile extends File{
    deadline?: string,
    isFinished: boolean
}
