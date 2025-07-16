// src/app/components/date-picker/date-picker.component.ts
import { Component, forwardRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, NG_VALUE_ACCESSOR, ReactiveFormsModule, FormControl } from '@angular/forms';

@Component({
  selector: 'app-date-picker',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <input
      type="date"
      class="input input-bordered w-full"
      [value]="dateValue"
      (change)="onChange($event)"
      (blur)="onTouched()"
    >
  `,
  styles: [],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DatePickerComponent),
      multi: true
    }
  ]
})
export class DatePickerComponent implements ControlValueAccessor {
  dateValue: string = '';

  // Function to call when the value changes
  onChange = (event: any) => {
    const newValue = event.target.value;
    this.dateValue = newValue;
    this.propagateChange(newValue);
  };

  // Function to call when the input is touched
  onTouched = () => {};

  // Method called by the forms API to write to the view
  writeValue(value: string): void {
    if (value) {
      // Expecting value in format 'YYYY-MM-DD'
      this.dateValue = value;
    } else {
      this.dateValue = '';
    }
  }

  // Method that registers a handler that should be called when the control value changes
  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  // Method that registers a handler specifically for when a control receives a touch event
  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  // The internal method to propagate changes back to the form
  private propagateChange = (_: any) => {};
}
