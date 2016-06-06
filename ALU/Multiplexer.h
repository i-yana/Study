#ifndef MULTIPLEXER_H
#define MULTIPLEXER_H

#include "Item.h"
#include "vector"

namespace ALU 
{
    class MultiPlexer: public Item 
	{
    public:
        MultiPlexer(): Item()
		{
            inputs.resize(8);
            type = MULTIPLEXER;
        };
        bool getValue();
        bool addInput(pItem);
        void addControlInput(pItem);
    private:
        std::vector<pItem> inputs;
        int dataInputs = 0;
    };
}

#endif
