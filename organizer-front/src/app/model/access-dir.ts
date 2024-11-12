export interface AccessDir {
    id: adId,
    accessPrivilege: number
}

export interface adId {
    userId: number,
    directoryId: number
}