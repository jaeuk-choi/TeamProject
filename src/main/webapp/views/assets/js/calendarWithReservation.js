class CalendarAndReservation {
	constructor() {
		this.selectedYear, this.selectedMonth, this.selectedDayNum;
		this.selectedDate;
		this.monthTable = {
			"January": 1,
			"February": 2, 
			"March": 3, 
			"April": 4, 
			"May": 5, 
			"June": 6, 
			"July": 7,
			"August": 8, 
			"September": 9, 
			"October": 10, 
			"November": 11, 
			"December": 12
		};
		
		this._initElement();
		this._init();
		this._setEventHandlers();
	}
	
	_initElement() {
		// 달력 오른쪽에 있는 "예약 현황" 카드 요소.
		this.currentReservation = document.querySelector("#current-reservation");
		[this.resDisplayMonth, this.resDisplayDate] = this.currentReservation
				.querySelectorAll("li:first-child span");
		// 이번 달에 해당하는 달력 칸 요소들.
		this.activeCells = document.querySelectorAll(".days > li:not(.inactive)");
		this.calendarUl = document.querySelector(".days");
		
		[this.selectedMonth, this.selectedYear] = document
					.querySelector(".current-date").textContent.split(" ");
	}
	
	_init() {
		let today = new Date();
		this.resDisplayMonth.textContent = today.getMonth() + 1;
		this.resDisplayDate.textContent = today.getDate();
	}
	
	_setEventHandlers() {
		this.calendarUl.addEventListener("click", event => {
			if (event.target.tagName !== "LI") return;
			
			this.selectedDayNum = event.target.textContent;
			[this.selectedMonth, this.selectedYear] = document
				.querySelector(".current-date").textContent.split(" ");
			this.selectedMonth = this.monthTable[this.selectedMonth];
			
			this.resDisplayMonth.textContent = this.selectedMonth;
			this.resDisplayDate.textContent = this.selectedDayNum;
			
			this.selectedDate = [
				this.selectedYear, 
				this.selectedMonth, 
				this.selectedDayNum
			].join("-");
			console.log(this.selectedDate);
		});
	}
}
console.log('hi'); // For test

new CalendarAndReservation();

