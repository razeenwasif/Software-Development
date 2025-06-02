from diceroll import roll_the_dice, special_roll
from helpers import generate_surprises
from typing import Dict, List, Union

def initialise_game() -> dict:
    """Initialize game structure with players, snakes, ladders, and surprises."""
    return {
        "players": {"Red": 0, "Blue": 0, "Green": 0, "White": 0},
        "snakes": {"25": 6, "44": 23, "65": 34, "76": 28, "99": 56},
        "ladders": {"8": 43, "26": 39, "38": 55, "47": 81, "66": 92},
        "surprise_tiles": generate_surprises()
    }

def get_num_players() -> int:
    """Get valid number of players from user input."""
    while True:
        try:
            num = int(input("Enter number of players (1-4): "))
            if 1 <= num <= 4:
                return num
            print("Please enter between 1-4")
        except ValueError:
            print("Invalid input. Enter a number.")

def play_game(game: Dict) -> Union[str, None]:
    """Main game loop with surprise tile functionality."""
    players = list(game["players"].keys())
    positions = game["players"]
    surprise_tiles = game.get("surprise_tiles", [])
    
    while True:
        for i, player in enumerate(players):
            current_pos = positions[player]
            
            # Roll and move
            roll = roll_the_dice()
            new_pos = current_pos + roll
            
            if new_pos > 100:
                continue
                
            positions[player] = handle_board_features(new_pos, game)
            
            # Check win condition
            if positions[player] == 100:
                return player
            
            # Handle surprises
            if positions[player] in surprise_tiles:
                handle_surprise(game, player, players, i)
            
            if positions[player] == 100:
                return player

def handle_board_features(pos: int, game: Dict) -> int:
    """Handle snake/ladder transitions."""
    if pos in game["snakes"]:
        new_pos = game["snakes"][pos]
        print(f"Snake! Slid to {new_pos}")
        return new_pos
    if pos in game["ladders"]:
        new_pos = game["ladders"][pos]
        print(f"Ladder! Climbed to {new_pos}")
        return new_pos
    return pos

def handle_surprise(game: Dict, current_player: str, players: List[str], current_index: int):
    """Handle surprise tile effects."""
    surprise_tiles = game.get("surprise_tiles", [])  # Safe access
    current_pos = game["players"][current_player]
    
    if current_pos in surprise_tiles:
        print(f"{current_player} triggered a surprise!")
        effect = special_roll()
    
        if effect == 0:
            print("Extra turn granted!")
            roll_again = roll_the_dice()
            new_pos = game["players"][current_player] + roll_again
            if new_pos <= 100:
                game["players"][current_player] = new_pos
                print(f"Extra roll: {roll_again}, new position: {new_pos}")
        elif effect == 1:
            next_index = (current_index + 1) % len(players)
            skipped = players[next_index]
            print(f"{skipped} loses their next turn!")
            players.append(players.pop(next_index))  # Move to end of turn order
        elif effect == 2:
            print("All others move back 5 spaces!")
            for p in players:
                if p != current_player:
                    game["players"][p] = max(game["players"][p] - 5, 0)

def pick_winner(players: Dict[str, int]) -> Union[str, None]:
    """Identify winning player if any."""
    for player, pos in players.items():
        if pos == 100:
            return player
    return None

def turn_by_turn_gameplay():
    """Manual 2-player game mode."""
    game = initialise_game()
    game["players"] = {name: pos for name, pos in list(game["players"].items())[:2]}
    players = list(game["players"].keys())
    
    while True:
        for player in players.copy():  # Use copy for safe iteration
            if player not in players:  # Skip removed players
                continue
                
            action = input(f"{player}'s turn: Enter 'roll' or 'quit': ").lower()
            if action == "quit":
                print("Game ended.")
                return
            if action != "roll":
                continue
                
            # Game logic similar to play_game but with manual input
            current_pos = game["players"][player]
            roll = roll_the_dice()
            new_pos = current_pos + roll
            
            if new_pos > 100:
                print("Overshot! Turn skipped.")
                continue
                
            game["players"][player] = handle_board_features(new_pos, game)
            print(f"Rolled {roll}, moved to {new_pos}")
            
            if game["players"][player] == 100:
                print(f"{player} wins!")
                return
            
            if game["players"][player] in game["surprise_tiles"]:
                handle_surprise(game, player, players, players.index(player))

def main():
    """Main game execution flow."""
    game = initialise_game()
    num_players = get_num_players()
    game["players"] = dict(list(game["players"].items())[:num_players])
    
    winner = play_game(game)
    print(f"Winner: {winner}" if winner else "No winner")
    
    print("\nStarting 2-player manual mode...")
    turn_by_turn_gameplay()

if __name__ == "__main__":
    main()
