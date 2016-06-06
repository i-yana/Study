#include "Offline_Creator.h"

namespace Games
{
	Interface* Offline_Creator::create_interface(const Arguments& arguments)
	{
		return new Offline(arguments);
	}

	Offline_Creator::Offline_Creator() {}

	Offline_Creator::~Offline_Creator() {}
}