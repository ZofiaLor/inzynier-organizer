export interface File {
    id: number,
    name: string,
    textContent: string,
    creationDate: string,
    parent?: number,
    owner: number
}
