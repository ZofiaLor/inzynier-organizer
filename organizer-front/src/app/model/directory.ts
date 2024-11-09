export interface Directory {
    id: number,
    name: string,
    parent: number | null,
    owner: number,
    files: number[]
}
