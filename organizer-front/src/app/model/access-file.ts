export interface AccessFile {
    id: afId,
    accessPrivilege: number
}

export interface afId {
    userId: number,
    fileId: number
}