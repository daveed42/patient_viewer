import {Component, Input} from '@angular/core';
import {FhirService} from '../services/fhir.service';
import {CarePlanService} from '../services/carePlan.service';
import {CarePlan} from '../models/carePlan.model';
import {Patient} from '../models/patient.model';

@Component({
    selector: 'carePlanChart',
    templateUrl: '/carePlanChart.html'
})
export class CarePlanChartComponent{

    selected: CarePlan;
    carePlans: Array<CarePlan>;
    @Input() patient: Patient;

    constructor(private fhirService: FhirService, private carePlanService: CarePlanService) {
        console.log("CarePlanComponent created...");
    }

	ngOnChanges() {
		this.selected = null;
		if (this.patient) {
			this.carePlanService.index(this.patient, true).subscribe(data => {
			if (data.entry){
				this.carePlans = <Array<CarePlan>>data.entry.map(r => r['resource']);
				console.log(this.carePlans[0]);
				this.carePlans = this.carePlans.reverse();
				console.log("Loaded " + this.carePlans.length + " carePlans.");
			}
			else{
				this.carePlans = new Array<CarePlan>();
				console.log("No carePlans for patient.");
			}
			});
		}
	}



}
