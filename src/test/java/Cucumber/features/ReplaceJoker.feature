@tag:
Feature: A player's operations regarding using joker
  This feature focuses on the rule 3 (matching tile must come from hand), as other restrictions have been covered in other features


  @replacing_joker_using_tiles_from_hand
    Scenario Outline: Player replaces Joker in the meld by playing his tiles from hand
    Given Table has <meld>
    And Player has <hand> in his hand
    And Player has played tiles before this turn
    When Player adds <tiles> to 1 meld
    Then The joker in meld 1 is replaceable
    Examples:
      | meld | hand | tiles |
      | "R2,R3,R4,*" | "R5,R6,R7" | "R5,R6"|
      | "R2,R3,R4,*" | "R1,R2,B7" | "R1"|
      | "R1,R2,*"    | "R3,G6,O8" | "R3"|
      | "R1,R2,*"    | "R13,G4,G8"| "R13"|

      | "R5,B5,G5,*" | "O5,G7"    | "O5" |
      | "R5,B5,*"    | "O5,O7"    | "O5" |
      | "R5,B5,*"    | "G5,O7"    | "G5" |
      | "R5,B5,*"    | "G5,O5"    | "G5,O5" |

  @trying_to_replace_joker_using_tiles_from_table
  Scenario Outline: Player tries to replace Joker in the meld by moving tiles from other melds on the table
    Given Table has <melds>
    And Player has played tiles before this turn
    When Player moves <move> from 2 meld to 1 meld
    Then The joker in meld 1 is not replaceable
    Examples:
      | melds | move |
      | "R2,R3,R4,*/R5,R6,R7"| "R5,R6" |
      | "R2,R3,R4,*/R1,R2,B7"| "R1"    |
      | "R1,R2,*/R3,G6,O8" | "R3"|
      | "R1,R2,*/R13,G4,G8"| "R13"|

      | "R5,B5,G5,*/O5,G7"    | "O5" |
      | "R5,B5,*/O5,O7"    | "O5" |
      | "R5,B5,*/G5,O7"    | "G5" |
      | "R5,B5,*/G5,O5"    | "G5,O5" |