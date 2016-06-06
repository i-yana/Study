#ifndef OFFLINE_CREATOR_H
#define OFFLINE_CREATOR_H
#include "Factory_of_Interfaces.h"
#include "Offline.h"

namespace Games
{
	class Offline_Creator : public Factory_of_Interfaces
	{
	public:
		Interface* create_interface(const Arguments& arguments) override;
		Offline_Creator();
		~Offline_Creator();
	};
}
#endif