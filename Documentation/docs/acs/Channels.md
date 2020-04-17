  

---
title: Channels
---

# Channels

Channels are the main way to transmit data from one component to another. A channel always transmits information from the output port to the input port. The data type of the channel is always equal the data type of the output port. The components of the AsTeRICS platform process one or several of the following data types, represented by the ports of the components:

*   Boolean: can be true or false
*   Byte: numbers from -128 to 127
*   Char: one character
*   Integer: numbers from approx. -2 billion to +2 billion
*   Double: huge amount of positive and negative floating point numbers
*   String: a string of characters (up to whole sentences)

The ports can be connected to ports with the same data type or following these connection rules:

*   byte to integer
*   byte to double
*   char to integer
*   char to double
*   integer to double
*   double to integer