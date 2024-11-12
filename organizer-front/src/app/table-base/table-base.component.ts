import { Component, EventEmitter, Input, OnChanges, OnInit, Output, ViewChild } from '@angular/core';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatSort, MatSortModule} from '@angular/material/sort';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';

@Component({
  selector: 'app-table-base',
  standalone: true,
  imports: [MatSortModule, MatTableModule, MatPaginatorModule, MatFormFieldModule, MatInputModule,],
  templateUrl: './table-base.component.html',
  styleUrl: './table-base.component.scss'
})
export class TableBaseComponent implements OnChanges{
  
  @Input() columns: string[] = [];
  @Input() data: object[] = [];
  @Output() selectEmitter = new EventEmitter<number>();
  dataSource : MatTableDataSource<object> = new MatTableDataSource<object>(this.data);
  clickedId?: number;

  @ViewChild(MatSort) set matSort(sort: MatSort) {
    this.dataSource.sort = sort;
  }

  @ViewChild(MatPaginator) set matPaginator(paginator: MatPaginator) {
    this.dataSource.paginator = paginator;
  }

  ngOnChanges(): void {
    if (this.data) {
      this.dataSource.data = this.data;
    }
  }

  clicked(id: number): void {
    this.clickedId = id;
    this.selectEmitter.emit(id);
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
