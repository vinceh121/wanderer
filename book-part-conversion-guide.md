# Story Book Conversion Guide

## Introduction

Project Nomads' story scripts are in TCL, while Wanderer's scripts are in JavaScript.

## Chapter definition

In Project Nomads, the chapter definition script goes under the chapter folder and loads parts.
In Wanderer, the chapter definition is the script that defines the entire story line and bears the canonical name of the story line. See `singleplayer.js`.

## Part definition

In Project Nomads, the part definition script goes into the `chapterXX/partXX` folder, and is named `story.tcl`.
In Wanderer, the part definition script goes into the `story/<storyline name>/chapterXX/partXX` folder, and is named index.js.

#### Method differences

> Each subsequence subsection title will have the name format `Project Nomads Method / Wanderer Method`

> For clarity sake, JS method definitions will be written in TS

###### daytime / setDayTime

```typescript
setDayTime(hours: number, minutes: number): void
```

##### on / addEventListener

```typescript
addEventListener(event: string, handler: (event: Event) => void): void
```

##### oneshottimer,oneshotaction / setTimeout

```typescript
setTimeout(handler: (args...: any[]) => void, delay: number, args...: any[]): number
```

##### timer  / setInterval

```typescript
setInterval(handler: (args...: any[]) => void, delay: number, args...: any[]): number
```
