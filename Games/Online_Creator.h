#ifndef ONLINE_CREATOR_H
#define ONLINE_CREATOR_H
#include "Factory_of_Interfaces.h"
#include "Online.h"

namespace Games
{
	class Online_Creator : public Factory_of_Interfaces
	{
	public:
		Interface* create_interface(const Arguments& arguments) override;
		Online_Creator();
		~Online_Creator();
	};
}
#endif