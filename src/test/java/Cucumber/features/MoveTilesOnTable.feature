@tag:
Feature: A player moves tiles in a meld to another on the table
  I want to use this feature to test the functionality of moving tiles by player

  @player_moves_tiles_from_one_meld_to_another
    Scenario Outline:  Player moves tiles from one meld to another
      Given  Table has <from>
      And Table has <to>
      And Player has played tiles before this turn
      When Player moves <move> from 1 meld to 2 meld
      Then the table has <new> now
    Examples:
      | from | to | move | new |
      | "R1,R2,R3,R4"  | "R5,R6"    | "R4"| "R1,R2,R3/R4,R5,R6"    |
      | "R1,R2,R3,R4"  | "R5,R6,R7" | "R4"| "R1,R2,R3/R4,R5,R6,R7" |
      | "R1,R2,R3,R4"  | "B1,G1"    | "R1"| "R1,B1,G1"             |
      | "R1,R2,R3,R4"  | "B4,G4,O4" | "R4"| "R1,R2,R3/R4,B4,G4,O4" |

      | "R3,B3,G3,O3"  | "B4,B5"    | "B3"| "R3,G3,O3/B3,B4,B5"    |
      | "R3,B3,G3,O3"  | "B4,B5,B6" | "B3"| "R3,G3,O3/B3,B4,B5,B6" |
      | "R3,B3,G3,O3"  | "B3,G3"    | "O3"| "R3,B3,G3/B3,G3,O3"    |
      | "R3,B3,G3,O3"  | "B3,G3,O3" | "R3"| "B3,G3,O3/R3,B3,G3,O3" |


  @player_moves_normal_tiles_after_replacing_joker
    Scenario Outline: Player moves tiles from one meld that the joker has been replaced to another
      Given Table has <from>
      And Table has <to>
      And Player has played tiles before this turn
      And Player has <tile> in his hand
      And Player replace joker in meld 1 with <tile>
      When Player moves <move> from 1 meld to 2 meld
      Then the table has <new> now
    Examples:
      | from | to | tile | move | new |
      | "R1,R2,R3,R4,R5,*" | "R3,R4" | "R6" | "R5" | "R1,R2,R3,R4,R6,*/R3,R4,R5"|
      | "R1,R2,R3,R4,*" | "R2,R3,R4" | "R5" | "R1" | "R2,R3,R4,R5,*/R1,R2,R3,R4"|
      | "R1,R2,R3,R4,*" | "G1,O1" | "R5" | "R1" | "R2,R3,R4,R5,*/R1,G1,O1"|
      | "R1,R2,R3,R4,*" | "B1,G1,O1" | "R5" | "R1" | "R2,R3,R4,R5,*/R1,B1,G1,O1"|

      | "R5,B5,O5,*" | "R3,R4" | "G5" | "R5" | "B5,G5,O5,*/R3,R4,R5"|
      | "R2,B2,G2,*" | "R3,R4,R5" | "O2" | "R2" | "B2,G2,O2,*/R2,R3,R4,R5"|
      | "R2,B2,G2,*" | "G2,O2" | "O2" | "B2" | "R2,G2,O2,*/B2,G2,O2"|
      | "R2,B2,G2,*" | "B2,G2,O2" | "O2" | "R2" | "B2,G2,O2,*/B2,G2,O2,*"|


  #invalid

  @select_tiles_that_meld_does_not_have
  Scenario: Player moves tiles that the meld does not have
    Given Table has "R8,R9,R10,R11"
    And Table has "R4,R5,R6"
    And Player has played tiles before this turn
    When Player moves "R7" from 1 meld to 2 meld
    Then the table still has "R8,R9,R10,R11/R4,R5,R6"


  @from_a_meld_that_does_not_on_the_table
  Scenario: Player selects a meld that is not in the table
    Given Table has "R8,B8,G8,O8"
    And Table has "O9,O10,O11"
    And Player has played tiles before this turn
    When Player moves "R10" from 3 meld to 2 meld
    Then the table still has "O9,O10,O11/R8,B8,G8,O8"


  @to_a_meld_that_does_not_on_the_table
  Scenario: Player selects a meld that is not in the table
    Given Table has "R6,R7,R8,R9,R10"
    And Table has "R11,R12,R13"
    And Player has played tiles before this turn
    When Player moves "R10" from 1 meld to 3 meld
    Then the table still has "R6,R7,R8,R9,R10/R11,R12,R13"

    
  @move_before_initial_30
  Scenario Outline: Player has not played tiles yet, but move the tiles on the table
    Given Table has <from>
    And Table has <to>
    And Player has not played any tile yet
    When Player moves <move> from 1 meld to 2 meld
    And the table still has <from>
    And the table still has <to>
    Examples:
      | from | to | move |
      | "R1,R2,R3,R4"  | "R5,R6"    | "R4"|
      | "R1,R2,R3,R4"  | "R5,R6,R7" | "R4"|
      | "R1,R2,R3,R4"  | "B1,G1"    | "R1"|
      | "R1,R2,R3,R4"  | "B4,G4,O4" | "R4"|

      | "R3,B3,G3,O3"  | "B4,B5"    | "B3"|
      | "R3,B3,G3,O3"  | "B4,B5,B6" | "B3"|
      | "R3,B3,G3,O3"  | "B3,G3"    | "O3"|
      | "R3,B3,G3,O3"  | "B3,G3,O3" | "R3"|


  @move_to_form_invalid_meld_at_the_end_of_turn
  Scenario Outline: Player moves the table to form an invalid meld, and end his turn
    Given Table has <from>
    And Table has <to>
    And Player has played tiles before this turn
    When Player moves <move> from 1 meld to 2 meld
    And Player ends his turn
    Then the table still has <from>
    And the table still has <to>
    Examples:
      | from | to | move |

      | "R5,R6,R7,R8" | "R7,R8,R9" | "R8" |
      | "R10,B10,G10" | "R7,R8,R9" | "B10"|

      | "O10,O11,O12,O13" | "R7,B7,G7" | "O10"     |
      | "R7,R8,R9,R10"    | "R7,B7,G7" | "R7"      |
      | "R7,R8,R9,R10"    | "R7,B7,G7,O7" | "R7"   |



  @leaving_meld(s)_does_not_valid_at_the_end_of_turn
  Scenario Outline: Player moves the table and leave an invalid meld, and end his turn
    Given Table has <from>
    And Table has <to>
    And Player has played tiles before this turn
    When Player moves <move> from 1 meld to 2 meld
    And Player ends his turn
    Then the table still has <from>
    And the table still has <to>
    Examples:
      | from | to | move |
      | "R5,R6,R7" | "R8,R9,R10" | "R7" |
      | "R5,R6,R7,R8" | "R8,R9,R10"| "R7" |

      | "R8,B8,G8"    | "R5,R6,R7" | "R8" |



  @move_tiles_includes_joker_to_another_meld_after_replacing_joker
    Scenario: Player moves jokers to another meld after replacing joker
    Given Table has "R3,R4,R5,R6,*"
    And Table has "R7,B7,G7"
    And Player has "R8" in his hand
    And Player replace joker in meld 1 with "R8"
    And Player has played tiles before this turn
    When Player moves "*" from 1 meld to 2 meld
    Then the table still has "R3,R4,R5,R6,*/R7,B7,G7"

  @move_tiles_before_replacing_joker
    Scenario: Player moves tiles from a meld with joker before replacing joker
    Given Table has "R3,R4,R5,R6,*"
    And Table has "B3,G3,O3"
    And Player has played tiles before this turn
    When Player moves "R3" from 1 meld to 2 meld
    Then the table still has "R3,R4,R5,R6,*/B3,G3,O3"