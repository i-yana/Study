#ifndef OUTPUTITEM_H
#define OUTPUTITEM_H

#include "Item.h"

namespace ALU 
{
    class OutputItem : public Item {
    public:
		OutputItem() : Item()
		{ 
			_input = nullptr;
            type = OUTPUT;
        };
        bool getValue();
        bool addInput(pItem);
    private:
        pItem _input;
    };
    
}

#endif 
