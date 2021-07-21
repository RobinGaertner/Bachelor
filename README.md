# Bachelor

This is a programm to analyze the efficiency of the Protocol described in https://eprint.iacr.org/2020/1307.pdf

For this analysis I securely implemented a the Damgard-Jurik Encryption method: https://github.com/cryptovoting/damgard-jurik
I securely implemented the protocols secMult, SDT and MPCT and I made dummy functions for the underlying functionalities.

To test the Protocols yourself, call:

for SDT:
choose a prime modulus
choose a treshold t

create a coordinator with the needed variables
Create a List list1 with 4t+2 numbers,
create 2 Polynomials mod modulus,
Polynomial 2 is monic,
The polynomials have the same Degree,
Create a list2, where the entries are poly1(inputList)/poly2(inputlist) (mod modulus)
encrypt list2,
call SDT(list2, list1, treshold) to see, if the degree of the polynomials is <t

Example in SDTTests

for MPCT:
choose a number n
choose a treshold t
choose a prime modulus
create a coordinator with the needed variables

create a List with length n for every party,
give every party a list,
create a newList with any values except 0,
call MPCT(newList, modulus) to see, if the setIntersection is >n-t


