#ifndef INPUTITEM_H
#define INPUTITEM_H

#include "Item.h"

namespace ALU 
{
    class InputItem : public Item
	{
    public:
        InputItem():Item()
		{
            type = INPUT;
        };
        bool getValue();
        void setValue(bool);
        bool addInput(pItem);
    };
}

#endif
