import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatChipsModule} from '@angular/material/chips';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatTooltipModule} from '@angular/material/tooltip';
import { FormsModule, FormBuilder,
  FormControl,Validators, ReactiveFormsModule } from '@angular/forms';
import { EventFile } from '../model/event';
import { StorageService } from '../service/storage.service';
import { EventDateService } from '../service/event-date.service';
import { VoteService } from '../service/vote.service';
import { EventDate } from '../model/event-date';
import { Vote } from '../model/vote';
import { Subject, takeUntil } from 'rxjs';
import { CommonModule } from '@angular/common';
import { User } from '../model/user';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-voting-panel',
  standalone: true,
  imports: [MatCardModule, CommonModule, MatButtonModule, MatIconModule, MatChipsModule,
    MatFormFieldModule, FormsModule, ReactiveFormsModule, MatInputModule, MatTooltipModule
  ],
  templateUrl: './voting-panel.component.html',
  styleUrl: './voting-panel.component.scss'
})
export class VotingPanelComponent implements OnInit {
  private readonly _destroy$ = new Subject<void>();
  @Input() event?: EventFile;
  @Output() backEmitter = new EventEmitter();
  @Output() changeDateEmitter = new EventEmitter();

  eds: EventDate[] = [];
  votes = new Map<number, Vote[]>; // number - EventDate ID
  user?: User;
  isOwner = false;

  form = this.fb.group({
    start: new FormControl('', Validators.required),
    end: new FormControl('')
  })

  constructor (private readonly storageService: StorageService, private readonly edService: EventDateService, private readonly voteService: VoteService,
    private readonly fb: FormBuilder, private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.fetchEventDates();
    this.user = this.storageService.getUser();
    this.isOwner = this.user!.id == this.event!.owner;
  }

  onAddEventDate(): void {
    if (!this.form.controls.start.value || this.form.controls.start.value == '' || this.form.controls.start.value == null) return;
    if (this.form.controls.end.value != null && this.form.controls.end.value != '') {
      var start = new Date(this.form.controls.start.value!);
      var end = new Date(this.form.controls.end.value!);
      if (start > end) {
        this.snackBar.open("The start of the event cannot be after the end!", "OK", {duration: 10000});
        return;
      }
    }
    let ed: EventDate = {id: 0, event: this.event!.id, votes: [], totalScore: 0, start: this.form.controls.start.value, end: this.form.controls.end.value!};
    this.createEventDate(ed);
  }

  onCastVote(edId: number, score: number): void {
    if (!this.user) return;
    var vote: Vote = {id: 0, eventDate: edId, user: this.user!.id, score: score};
    this.createVote(vote);
  }

  onDelete(edId: number) {
    this.voteService.getCurrentUserVoteByEventDateId(edId).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.deleteVote(resp.body!.id);
      },
      error: err => {
        this.snackBarErrors(err, "Event date ");
      }
    })
  }

  confirmEventDate(edId: number) {
    this.edService.getEventDateById(edId).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.event!.startDate = resp.body!.start;
        this.event!.endDate = resp.body!.end;
        this.changeDateEmitter.emit();
      },
      error: err => {
        this.snackBarErrors(err, "Event date ");
      }
    })
  }

  onDeleteEventDate(edId: number) {
    this.edService.deleteEventDate(edId).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.fetchEventDates();
      },
      error: err => {
        this.snackBarErrors(err, "Event date ");
      }
    })
  }

  fetchEventDates() {
    if (!this.event) return;
    this.edService.getEventDatesByEventId(this.event.id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.eds = resp.body!;
        for (var ed of this.eds) {
          this.fetchVotesForEventDate(ed.id);
        }
      },
      error: err => {
        this.snackBarErrors(err, "Event date ");
      }
    })
  }

  fetchVotesForEventDate(edId: number) {
    this.voteService.getVotesByEventDateId(edId).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.votes.set(edId, resp.body!);
      },
      error: err => {
        this.snackBarErrors(err, "Vote ");
      }
    })
  }

  createEventDate(ed: EventDate) {
    this.edService.createEventDate(ed).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.fetchEventDates();
      },
      error: err => {
        this.snackBarErrors(err, "Vote ");
      }
    })
  }

  createVote(vote: Vote) {
    this.voteService.createVote(vote).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.fetchEventDates();
      },
      error: err => {
        this.snackBarErrors(err, "Vote ");
      }
    })
  }

  deleteVote(id: number) {
    this.voteService.deleteVote(id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.fetchEventDates();
      },
      error: err => {
        this.snackBarErrors(err, "Vote ");
      }
    })
  }

  snackBarErrors(err: any, element: string) {
    if (err.status == 404) {
      this.snackBar.open(element + " not found", undefined, {duration: 3000});
    } else {
      this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
    }
  }

}
