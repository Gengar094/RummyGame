@tag:
Feature: A player moves tiles in a meld to another on the table
  I want to use this feature to test the functionality of splitting meld by player

@player_split_a_meld
  Scenario Outline: Player splits a meld on the table
    Given Table has <meld>
    And Player has played tiles before this turn
    When Player splits the 1 meld into <pieces>
    And Player does not end his turn
    Then the table does not have <meld>
    And the table has <pieces> now
  Examples:
    | meld | pieces |
    | "R1,R2,R3,R4"| "R1/R2,R3/R4"|
    | "R1,R2,R3"   | "R1/R2/R3"   |
    | "B1,B2,B3,B4,B5,B6,B7,B8,B9,B10,B11,B12,B13" | "B1,B2,B3/B4,B5,B6/B6,B7,B8,B9/B10,B11,B12,B13"|
    | "R4,B4,G4"   | "R4/B4,G4"   |
    | "R4,B4,G4,O4"| "R4/B4/G4/O4"|

@split_after_replacing_joker
Scenario Outline: Player splits a meld including joker on the table after replacing joker
  Given Table has <meld>
  And Player has <replace> in his hand
  And Player has played tiles before this turn
  And Player replace joker in meld 1 with <replace>
  When Player splits the 1 meld into <pieces>
  And Player does not end his turn
  Then the table does not have <meld>
  And the table has <pieces> now
  Examples:
    | meld | replace | pieces |
    | "R3,R4,*"| "R5"| "R3,R5/R4,*"|
    | "B1,B2,B3,B4,B5,B6,B7,B8,B9,B10,B11,B12,*" | "B13" | "B1,B2,B3/B4,B5,B6/B6,B7,B8,B9/B10,B11,B12,B13,*"|
    | "R3,R4,R5,*" | "R6"| "R3,R4,R5/R6,*"|
    | "R3,R5,R6,*" | "R4"| "R3,R4/R5,R6,*"|

    | "R3,B3,*"    | "G3"|  "R3/B3,G3,*"  |
    | "R3,B3,G3,*" | "O3"| "R3,B3/G3,O3,*"|


@split_not_existing_meld
  Scenario: Player splits a meld that table does not have
    Given Table has "R1,R2,R3"
    And Player has played tiles before this turn
    When Player splits the 2 meld into "R1,R2/R3"
    And Player does not end his turn
    Then the table does not have "R1,R2/R3"
    And the table still has "R1,R2,R3"

@split_with_tiles_that_meld_does_not_have
  Scenario: Player splits a meld into smaller melds using tiles that origin meld does not have
    Given Table has "R1,R2,R3"
    And Player has played tiles before this turn
    When Player splits the 1 meld into "R1,R2/R4"
    And Player does not end his turn
    Then the table does not have "R1,R2/R4"
    And the table still has "R1,R2,R3"

@split_to_form_invalid_meld_at_the_end_of_turn
  Scenario Outline: Player splits a meld into one or more invalid melds, and end his turns
    Given Table has <meld>
    And Player has played tiles before this turn
    When Player splits the 1 meld into <pieces>
    And Player ends his turn
    Then the table does not have <pieces>
    And the table still has <meld>
  Examples:
    | meld | pieces |
    | "R1,R2,R3,R4,R5" | "R1,R2,R3/R4,R5"|
    | "R1,R2,R3" | "R1/R2,R3"|
    | "R1,R2,R3,R4,R5,R6" | "R1,R3,R4/R2,R5,R6"|

    | "R3,B3,O3"          | "R3,B3/O3"         |
    | "R3,B3,O3,G3"       | "R3,B3,O3/G3"      |

  #ok
@split_before_initial_30
  Scenario: Player splits a meld before playing initial 30
    Given Table has "R1,R2,R3"
    And Player has not played any tile yet
    When Player splits the 1 meld into "R1,R2/R3"
    And Player does not end his turn
    Then the table does not have "R1,R2/R3"
    And the table still has "R1,R2,R3"

@split_before_replacing_joker
  Scenario: Player splits a meld including joker before replacing joker
    Given Table has "R1,R2,R3,*"
    And Player has played tiles before this turn
    When Player splits the 1 meld into "R1,R2/R3,*"
    And Player does not end his turn
    Then the table does not have "R1,R2/R3,*"
    And the table still has "R1,R2,R3,*"

