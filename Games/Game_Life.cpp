#include "Game_Life.h"
#include "Online_Creator.h"
#include "Offline_Creator.h"

namespace Games
{
	void Game_Life::run()
	{
		arg = parser.parse_arguments(keys);
		select_interface();
		Executor executor(arg.universe, interface, arg.iteratons, arg.strategy);
		interface->play(executor);
	}

	void Game_Life::select_interface()
	{
		if (arg.out_file.empty() && arg.iteratons == 0)
		{
			factory_of_interfaces.reset(new Online_Creator);
			interface.reset(factory_of_interfaces->create_interface(arg));
		}
		else
		{
			factory_of_interfaces.reset(new Offline_Creator);
			interface.reset(factory_of_interfaces->create_interface(arg));
		}
	}
	Game_Life::~Game_Life()	{}
}