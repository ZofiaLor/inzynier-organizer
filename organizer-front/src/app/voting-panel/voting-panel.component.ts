import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatChipsModule} from '@angular/material/chips';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
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

@Component({
  selector: 'app-voting-panel',
  standalone: true,
  imports: [MatCardModule, CommonModule, MatButtonModule, MatIconModule, MatChipsModule,
    MatFormFieldModule, FormsModule, ReactiveFormsModule, MatInputModule
  ],
  templateUrl: './voting-panel.component.html',
  styleUrl: './voting-panel.component.scss'
})
export class VotingPanelComponent implements OnInit {
  private readonly _destroy$ = new Subject<void>();
  @Input() event?: EventFile;
  @Output() backEmitter = new EventEmitter();

  eds: EventDate[] = [];
  votes = new Map<number, Vote[]>; // number - EventDate ID
  user?: User;

  form = this.fb.group({
    start: new FormControl('', Validators.required),
    end: new FormControl('')
  })

  constructor (private readonly storageService: StorageService, private readonly edService: EventDateService, private readonly voteService: VoteService,
    private readonly fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.fetchEventDates();
    this.user = this.storageService.getUser();
  }

  onAddEventDate(): void {
    console.log(this.form.controls.start.value);
    if (!this.form.controls.start.value || this.form.controls.start.value == '' || this.form.controls.start.value == null) return;
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
        console.log(err);
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
        console.log(err);
      }
    })
  }

  fetchVotesForEventDate(edId: number) {
    this.voteService.getVotesByEventDateId(edId).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.votes.set(edId, resp.body!);
      },
      error: err => {
        console.log(err);
      }
    })
  }

  createEventDate(ed: EventDate) {
    this.edService.createEventDate(ed).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.fetchEventDates();
      },
      error: err => {
        console.log(err);
      }
    })
  }

  createVote(vote: Vote) {
    this.voteService.createVote(vote).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.fetchEventDates();
      },
      error: err => {
        console.log(err);
      }
    })
  }

  deleteVote(id: number) {
    this.voteService.deleteVote(id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.fetchEventDates();
      },
      error: err => {
        console.log(err);
      }
    })
  }


}
