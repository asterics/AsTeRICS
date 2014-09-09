/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * EyeX.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_TX__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_TX__HPP__

/*********************************************************************************************************************/

#define TX_NAMESPACE_BEGIN namespace EyeX {
#define TX_NAMESPACE_END }

/*********************************************************************************************************************/

#include <vector>
#include <memory>
#include <functional>
#include <fstream>
#include <map>
#include <algorithm>
#include <sstream>
#include <cassert>

/*********************************************************************************************************************/

#define TOBII_TX_DETAIL

#include "eyex/EyeX.h"

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN 
	
class InteractionSystem;
class InteractionObject;
class Property;
class PropertyBag;
class InteractionBounds;
class InteractionBehavior;
class Interactor;
class InteractionSnapshot;
class InteractionSnapshotResult;
class InteractionCommand;
class InteractionCommandResult;
class InteractionQuery;
class InteractionEvent;
class InteractionNotification;
class AsyncData;
class StateBag;
class InteractionMask;

TX_NAMESPACE_END

/*********************************************************************************************************************/

#include "APIException.hpp"
#include "Callbacks.hpp"
#include "InteractionSystem.hpp"
#include "PropertyValueResolver.hpp"
#include "InteractionContext.hpp"
#include "HandleWrapper.hpp"
#include "InteractionObject.hpp"
#include "InteractionBehavior.hpp"
#include "InteractionBounds.hpp"
#include "InteractionCommand.hpp"
#include "AsyncData.hpp"
#include "InteractionEvent.hpp"
#include "InteractionQuery.hpp"
#include "InteractionSnapshot.hpp"
#include "Interactor.hpp"
#include "Property.hpp"
#include "PropertyBag.hpp"
#include "InteractionNotification.hpp"
#include "StateBag.hpp"
#include "InteractionMask.hpp"
#include "InteractionAgentBase.hpp"

/*********************************************************************************************************************/

#include "PropertyValueResolver.inl"
#include "InteractionSystem.inl"
#include "InteractionContext.inl"
#include "HandleWrapper.inl"
#include "InteractionObject.inl"
#include "InteractionBehavior.inl"
#include "InteractionBounds.inl"
#include "InteractionCommand.inl"
#include "AsyncData.inl"
#include "InteractionEvent.inl"
#include "InteractionQuery.inl"
#include "InteractionSnapshot.inl"
#include "Interactor.inl"
#include "Property.inl"
#include "PropertyBag.inl"
#include "InteractionNotification.inl"
#include "StateBag.inl"
#include "InteractionMask.inl"
#include "InteractionAgentBase.inl"

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_TX__HPP__)

/*********************************************************************************************************************/
