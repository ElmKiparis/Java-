import { Component, OnInit, NgModule } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { ApiService } from '../service/api.service';

import { BACKEND_URL, STATIC_FILES_PATH } from './config';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})

export class AppComponent implements OnInit {

  title = 'Lab 2';

  staticFilesPath = STATIC_FILES_PATH;
  backendUrl = BACKEND_URL;

  object: any;

  selectedFiles?: FileList;

  name: string = '';
  age: string = '';

  constructor(private apiService: ApiService) { }

  ngOnInit() {
    this.apiService.getObject().subscribe(data => {
      this.object = data;
      this.name = this.object.name;
      this.age = this.object.age;
    });
  }

  onFileSelected(event: any): void {
    this.selectedFiles = event.target.files;
  }

  onUpload(): void {

    if (this.selectedFiles) {

      const formData = new FormData();
      for (let i = 0; i < this.selectedFiles.length; i++) {
        formData.append("files", this.selectedFiles[i]);
      }

      this.apiService.updateObject(formData).subscribe(
        data => console.log('Upload successful', data),
        error => console.error('Error uploading files', error)
      );

      location.reload();

    }

  }

  onChangeName(event: any): void {
    this.name = event.target.value;
  }

  onChangeAge(event: any): void {
    this.age = event.target.value;
  }

  onSubmit(): void {

    const formData = new FormData();

    formData.append('name', this.name);
    formData.append('age', this.age);

    this.apiService.updateObject(formData).subscribe(
      data => console.log('Save successful', data),
      error => console.error('Error saving object', error)
    );

    location.reload();

  }
  
}
