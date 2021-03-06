import { Component, Input, OnDestroy, Output,EventEmitter  } from '@angular/core';
import {DraggableWidget} from './draggable_widget.component';
import {NgGrid, NgGridItem, NgGridConfig, NgGridItemConfig, NgGridItemEvent} from 'angular2-grid';
import {Subscription} from 'rxjs/Subscription';
import {DoctorService} from '../services/doctor.service';
import {ScratchPadService} from '../services/scratchPad.service';
import {LoupeService} from '../services/loupe.service';
import {Patient} from '../models/patient.model';
import {Condition} from '../models/condition.model';

@Component({
    selector: 'scratchPad',
    templateUrl: 'scratchPad.html'
})

export class ScratchPadComponent implements OnDestroy{
    @Input() dataSource: Array<any>;
    @Output() totalUpdate = new EventEmitter();
    currentDataArray: Array<any> = [];
    subscription: Subscription;
    lastIndex: number;
    constructor(private doctorService: DoctorService, private scratchPadService: ScratchPadService, private loupeService: LoupeService) {
        console.log("ScratchPadComponent Created...");
        this.subscription = this.scratchPadService.addNewData$.subscribe(clicked => {
          console.log("value:"+clicked);
            this.addToScratchPad();
        });
    }


    checked(selected:any, event, position, data) {
      selected.isSelected = !selected.isSelected;
      if(event.shiftKey) {
  			let upper,lower;
  			if(position<this.lastIndex) {
  				upper = this.lastIndex;
  				lower = position;
  			}
  			else {
  				upper = position;
  				lower = this.lastIndex;
  			}
  			for(let i = lower; i<=upper; i++) {
  				data[i].isSelected = true;
  			}
  		}
  		this.lastIndex = position;
      }

    addToScratchPad(){
      console.log("adding");

        for (let c of this.dataSource){
            if (!this.doesExist(c)){
                this.currentDataArray.push({"name":c.name,"date":c.date,"code":c.code});
            }
            else{
                console.log("This Condition already exists on the scratchPad, duplicates are not allowed");
            }
        };
        this.totalUpdate.emit(this.currentDataArray.length);

        // Recursively subscribes, do NOT change the onDestroy method or there will be memory leaks
/*
        this.subscription = this.scratchPadService.addNewData$.subscribe(clicked => {
            this.addToScratchPad();
        });
*/
    }
    doesExist(value) {
        for(let o of this.currentDataArray) {
          if(value.code==o.code) {
            return true;
        }
      }
      return false;
    }

    ngOnDestroy(){
        this.subscription.unsubscribe();
    }

    removeFromScratchPad(){
      for(let c = this.currentDataArray.length - 1; c >= 0; c--) {

          if(this.currentDataArray[c].isSelected) {
            this.currentDataArray.splice(c,1);
          }
        }
        this.totalUpdate.emit(this.currentDataArray.length);

    }


}
