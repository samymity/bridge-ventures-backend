package com.example.demo;
interface Printable 

{ default void print()


{ System.out.println(this);
} 

static void create() 
{

} 
}