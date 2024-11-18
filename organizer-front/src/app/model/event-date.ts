export interface EventDate {
    id: number,
    event: number,
    votes: number[],
    totalScore: number,
    start: string,
    end?: string
}
