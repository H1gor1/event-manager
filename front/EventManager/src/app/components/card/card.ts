
import { Component, Input, EventEmitter, Output } from '@angular/core';
import {NgClass, NgIf, NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-card',
  imports: [
    NgOptimizedImage,
    NgClass,
    NgIf
  ],
  templateUrl: './card.html',
  styleUrl: './card.css'
})
export class Card {
  @Input() title: string = 'Card Title';
  @Input() description: string = 'A card component has a figure, a body part, and inside body there are title and actions parts';
  @Input() imageUrl: string = 'https://img.daisyui.com/images/stock/photo-1606107557195-0e29a4b5b4aa.webp';
  @Input() imageAlt: string = 'Card Image';
  @Input() buttonText: string = 'Buy Now';
  @Input() showButton: boolean = true;
  @Input() cardWidth: string = 'w-96';

  @Output() buttonClick = new EventEmitter<void>();

  onButtonClick(): void {
    this.buttonClick.emit();
  }
}
