export interface Directory {
    id: number,
    name: string,
    parent: number | null,
    owner: number,
    children: number[],
    files: number[]
}
