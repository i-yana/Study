#ifndef GAME_H
#define GAME_H

namespace Games
{
	class Game
	{
	public:
		virtual void run() = 0;
		virtual void parse_argument() = 0;
		virtual ~Game() {};
	};
}
#endif
