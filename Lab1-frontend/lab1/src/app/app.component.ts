import { Component, OnInit } from '@angular/core';
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

  title = 'Lab 7';

  staticFilesPath = STATIC_FILES_PATH;
  backendUrl = BACKEND_URL;

  objects: any;

  selectedFiles?: FileList;
  uploadId: string = '';

  nameToAdd: string = '';
  ageToAdd: string = '';

  idToUpdate: string = '';
  nameToUpdate: string = '';
  ageToUpdate: string = '';

  constructor(private apiService: ApiService) { }

  ngOnInit() {
    this.apiService.getObjects().subscribe(data => {
      this.objects = data['_embedded']['persons'];
    });
  }

  onFileSelected(event: any): void {
    this.selectedFiles = event.target.files;
    let splitted = event.target.getAttribute('id').split('-');
    this.uploadId = splitted[splitted.length - 1];
  }

  onUpload(): void {

    if (this.selectedFiles) {

      const formData = new FormData();
      
      formData.append("avatar", this.selectedFiles[0]);

      this.apiService.uploadAvatar(Number(this.uploadId), formData).subscribe(
        data => console.log('Upload successful', data),
        error => console.error('Error uploading files', error)
      );

      location.reload();

    }

  }

  onDelete(id: number): void {

    this.apiService.deleteObject(id).subscribe(
      data => console.log('Delete successful', data),
      error => console.error('Error deleting object', error)
    );

    location.reload();

  }

  onChangeNameToAdd(event: any): void {
    this.nameToAdd = event.target.value;
  }

  onChangeAgeToAdd(event: any): void {
    this.ageToAdd = event.target.value;
  }

  onChangeIdToUpdate(event: any): void {
    this.idToUpdate = event.target.value;
  }

  onChangeNameToUpdate(event: any): void {
    this.nameToUpdate = event.target.value;
  }

  onChangeAgeToUpdate(event: any): void {
    this.ageToUpdate = event.target.value;
  }

  onAddSubmit(): void {

    const person = {
      name: this.nameToAdd,
      age: Number(this.ageToAdd)
    };

    this.apiService.addObject(person).subscribe(
      data => console.log('Save successful', data),
      error => console.error('Error saving object', error)
    );

    location.reload();

  }

  onUpdateSubmit(): void {

    const person = {
      name: this.nameToUpdate,
      age: Number(this.ageToUpdate)
    };

    this.apiService.updateObject(Number(this.idToUpdate), person).subscribe(
      data => console.log('Save successful', data),
      error => console.error('Error saving object', error)
    );

    location.reload();

  }
  
}
