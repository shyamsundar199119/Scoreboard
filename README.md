# Live Football World Cup Score Board

## Overview

This project is a simple Java library for managing live football World Cup scoreboards. The library allows users to start matches, update scores, finish matches, and retrieve a summary of all ongoing matches, ordered by their total score and start time.

## Features

- Start a new match with initial score 0-0.
- Update the score of an ongoing match.
- Finish an ongoing match.
- Get a summary of matches in progress, ordered by total score and start time.

## Prerequisites

- Java 11
- Maven

## Design Decisions
  In-Memory Storage: The library uses an in-memory list to store match information.
  Ordering: Matches are ordered by their total score and start time in the summary.

## Assumptions

- Teams cannot play in multiple matches simultaneously.
- Scores can only be updated with values greater than or equal to the current scores.
- The library correctly handles cases where home and away teams are interchanged.

## Dependencies

- Spring Boot 2.7.5
- JUnit 5.8.2