import { AbstractControl } from "@angular/forms";

export function celularValidator(control: AbstractControl) {
  const value = control.value?.toString().trim();
  return /^\d{10}$/.test(value) ? null : { celularInvalido: true };
}
/*
Usamos pattern(/^\d{10}$/) que significa:

Solo números (\d)

Exactamente 10 dígitos ({10})

El input type="number" permite números más largos, pero el validator lo bloquea.

Si querés permitir 11 dígitos (como muchas líneas argentinas con 15), solo cambiás {10} → {10,11}.
*/