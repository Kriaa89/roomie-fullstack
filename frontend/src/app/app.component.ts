import { RouterOutlet } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { ApiService } from './api.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'frontend';
  data?: string;
  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.getLanding().subscribe(res => this.data = res);
  }
}
