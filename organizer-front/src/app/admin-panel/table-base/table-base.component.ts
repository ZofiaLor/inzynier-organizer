import { Component, EventEmitter, Input, OnChanges, OnInit, Output, ViewChild } from '@angular/core';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatSort, MatSortModule} from '@angular/material/sort';

@Component({
  selector: 'app-table-base',
  standalone: true,
  imports: [MatSortModule, MatTableModule],
  templateUrl: './table-base.component.html',
  styleUrl: './table-base.component.scss'
})
export class TableBaseComponent implements OnChanges{
  
  @Input() columns: string[] = [];
  @Input() data: object[] = [];
  @Output() selectEmitter = new EventEmitter<number>();
  dataSource : MatTableDataSource<object> = new MatTableDataSource<object>(this.data);

  @ViewChild(MatSort) set matSort(sort: MatSort) {
    this.dataSource.sort = sort;
  }

  ngOnChanges(): void {
    if (this.data) {
      this.dataSource.data = this.data;
    }
  }

  clicked(id: number): void {
    this.selectEmitter.emit(id);
  }
}
