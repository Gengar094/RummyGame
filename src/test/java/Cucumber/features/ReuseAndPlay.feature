@tag:
Feature: A player reuses the existing tiles on the table and play tiles from his hand
  I want to use this feature to test the functionality of reusing tiles by players


@reuse_the_meld_from_the_table
  Scenario Outline: Player reuses the meld from the table
  Given Player has <tiles> in his hand
  And Table has <melds>
  And Player has played tiles before this turn
  When Player reuses <reuse> from 1 meld, and play <tiles>
  Then Player does not have <tiles> in his hand
  And Table has <new>
  And Table has <left>
  Examples:
    | tiles | melds | reuse | new | left |
    | "R11,B11"     | "R11,B11,G11,O11"        |   "G11"             |   "R11,B11,G11"            |  "R11,B11,O11"      |
    |  "R11,R12"    | "R13,B13,G13,O13"        |   "R13"             |  "R11,R12,R13"             |   "B13,G13.O13"     |
    |  "B13,O13"    |   "R10,R11,R12,R13"      |       "R13"         |    "R13,B13,O13"           |      "R10,R11,R12"  |
    |  "R11,R12"    |   "R7,R8,R9,R10"         |       "R10"         |        "R10,R11,R12"       |         "R7,R8,R9"  |

@reuse_other_tiles_in_meld_after_replacing_joker
  Scenario: Player reuses the non-joker tiles in a meld after replacing the joker
  Given Player has "R3,R4,R8" in his hand
  And Table has "R5,R6,R7,*"
  And Player has played tiles before this turn
  When Player replace joker in meld 1 with "R8"
  And Player reuses "R5" from 1 meld, and play "R3,R4"
  Then Player does not have "R3,R4,R8" in his hand
  And Table has "R3,R4,R5/R6,R7,R8"


@reuse_the_joker_after_replacing_it
  Scenario: Player reuses the joker after replacing it from the table
  Given Player has "R3,R4,R8" in his hand
  And Table has "R5,R6,R7,*"
  And Player has played tiles before this turn
  When Player replace joker in meld 1 with "R8"
  And Player reuses "*" from 1 meld, and play "R3,R4"
  Then Player does not have "R3,R4,R8" in his hand
  And Table has "R3,R4,*"

#invalid

@reuse_tiles_that_player_does_not_have
  Scenario: Player reuses the meld, but plays tiles that he does not have
  Given Player does not have tiles "R6" in his hand
  And Table has "R7,R8,R9,R10,R11"
  When Player reuses "R7,R8" from 1 meld, and play "R7,R8,R9,R10,R11"
  Then the table does not have "R6,R7,R8/R9,R10,R11"

@reuse_tiles_that_meld_does_not_have
  Scenario: Player reuses the non-existed tile from meld
  Given Player has "R6,R7,R8,R9" in his hand
  And Table has "R2,R3,R4"
  When Player reuses "R5" from 1 meld, and play "R6,R7,R8,R9"
  Then Player still has "R6,R7,R8,R9" in his hand
  And the table does not have "R2,R3,R4,R5/R6,R7,R8,R9"

@select_a_meld_that_does_not_on_the_table
Scenario: Player select a meld that is not in the table
  Given Table has "R6,R7,R8,R9/R1,R2,R3"
  And Player has "R4,R5" in his hand
  When Player reuses "R3" from 3 meld, and play "R4,R5"
  Then Player still has "R4,R5" in his hand
  And the table does not have "R4,R5"

@reuse_before_initial_30
  Scenario: Player has not played tiles yet, but reuse the tiles on the table
  Given Table has "R6,R7,R8,R9,R10"
  And Player has not played any tile yet
  And Player has "R5" in his hand
  When Player reuses "R6,R7" from 1 meld, and play "R6,R7,R8,R9,R10"
  Then Player still has "R5" in his hand
  And the table does not have "R5,R6,R7/R8,R9,R10"

@form_invalid_meld_at_the_end_of_turn
  Scenario Outline: Player reuses the table to form an invalid meld
  Given Player has <tiles> in his hand
  And Table has <melds>
  And Player has played tiles before this turn
  When Player reuses <reuse> from 1 meld, and play <tiles>
  Then Player still has <tiles> in his hand
  And the table does not have <new>
  Examples:
    | tiles | melds | reuse | new |
    | "R10"      |   "R9,R10,R11,R12"    |   "R10,R11,R12"    |  "R10,R10,R11,R12"   |
    | "O6"       |  "R7,B7,G7"        |   "R7,B7,G7"       |  "R7,B7,G7,O6"       |
    | "R7,*"     |  "R6,G6,B6,O6"     |   "B6"              | "R7,B6,*"           |
    | "R10,R11,*"    |  "R9,R10,R11,R12"| "R11"             | "R10,R11,R11,*"     |

    | "R10,B11"  |  "R11,R12,R13"     |   "R12,R13"        |  "R10,B11,R12,R13"   |
    | "G7"       |  "R7,B7,G7"        |   "R7,B7,G7"       |  "R7,B7,G7,G7"       |

    | "R7"       |  "R8,R9,R10,R11"   |   "R8"             |  "R7,R8"             |
    | "R7"       |  "R7,B7,G7,O7"     |   "B7"             |  "R7,B7"             |
    | "O7"       |  "R7,B7,G7,O7"   |    "R7,B7,G7,O7"     |  "R7,B7,G7,O7,O7"    |
    | "*"        |  "R7,B7,G7,O7"   |      "R7"            |  "R7,*"              |
    | "O7,*"     | "R7,B7,G7,O7"    |   "R7,B7,G7"         |  "R7,B7,G7,O7,*"     |



  @leaving_meld(s)_does_not_valid
Scenario Outline: Player reuses the table to form an valid meld, but left an invalid meld on the table
  Given Player has <tiles> in his hand
  And Table has <melds>
  When Player reuses <reuse> from 1 meld, and play <tiles>
  Then Player still has <tiles> in his hand
  And the table does not have <new>
  Examples:
    | tiles | melds | reuse | new |
    | "R7"  | "R8,R9,R10" | "R8,R9" | "R7,R8,R9" |
    | "R7"  | "R7,B7,G7,O7" | "B7,G7" | "R7,B7,G7" |

    | "R6,R7" | "R4,R5,R6,R7,R8" | "R5" | "R5,R6,R7"|

  #ok
@reuse_joker_before_replacing_it
Scenario: Player reuses the joker before replacing it from the table
  Given Player has "R3,R4" in his hand
  And Table has "R4,R5,R6,*"
  And Player has played tiles before this turn
  When Player does not replace joker with a tile
  And Player reuses "*" from 1 meld, and play "R3,R4"
  Then Player still has "R3,R4" in his hand
  And the table does not have "R3,R4,*"

  #ok
@reuse_other_tiles_with_joker_before_replacing_it_in_meld
Scenario: Player reuses the non-joker tiles in a meld before replacing the joker
  Given Player has "R3,R4" in his hand
  And Table has "R5,R6,R7,*"
  And Player has played tiles before this turn
  When Player does not replace joker with a tile
  And Player reuses "R5" from 1 meld, and play "R3,R4"
  Then Player still has "R3,R4" in his hand
  And the table does not have "R3,R4,R5/R6,R7,*"


