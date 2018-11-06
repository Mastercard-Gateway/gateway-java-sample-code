import { By } from '@angular/platform-browser';

/*
* Helper utility functions
*/
export function getElement(fixture, selector) {
	let elem = fixture.debugElement.query(By.css(selector));
	return elem ? elem.nativeElement : elem;
};

export function inputValue(fixture, selector, value) {
	let element = getElement(fixture, selector);
	element.value = value;
	element.dispatchEvent(new Event('input'));
}

export function getElementText(fixture, selector) {
	return getElement(fixture, selector).textContent;
}
