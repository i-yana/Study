#include "Online_Creator.h"

namespace Games
{
	Interface* Online_Creator::create_interface(const Arguments&arguments)
	{
		return new Online(arguments);
	}

	Online_Creator::Online_Creator() {}

	Online_Creator::~Online_Creator() {}
}