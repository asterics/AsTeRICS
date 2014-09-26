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
	
class Environment;
class InteractionObject;
class Property;
class PropertyBag;
class Bounds;
class Behavior;
class Interactor;
class Snapshot;
class SnapshotResult;
class Command;
class CommandResult;
class Query;
class InteractionEvent;
class Notification;
class AsyncData;
class StateBag;
class Mask;

TX_NAMESPACE_END

/*********************************************************************************************************************/

#include "APIException.hpp"
#include "Callbacks.hpp"
#include "Environment.hpp"
#include "PropertyValueResolver.hpp"
#include "Context.hpp"
#include "HandleWrapper.hpp"
#include "InteractionObject.hpp"
#include "Behavior.hpp"
#include "Bounds.hpp"
#include "Command.hpp"
#include "AsyncData.hpp"
#include "InteractionEvent.hpp"
#include "Query.hpp"
#include "Snapshot.hpp"
#include "Interactor.hpp"
#include "Property.hpp"
#include "PropertyBag.hpp"
#include "Notification.hpp"
#include "StateBag.hpp"
#include "Mask.hpp"
#include "InteractionAgentBase.hpp"

/*********************************************************************************************************************/

#include "PropertyValueResolver.inl"
#include "Environment.inl"
#include "Context.inl"
#include "HandleWrapper.inl"
#include "InteractionObject.inl"
#include "Behavior.inl"
#include "Bounds.inl"
#include "Command.inl"
#include "AsyncData.inl"
#include "InteractionEvent.inl"
#include "Query.inl"
#include "Snapshot.inl"
#include "Interactor.inl"
#include "Property.inl"
#include "PropertyBag.inl"
#include "Notification.inl"
#include "StateBag.inl"
#include "Mask.inl"
#include "InteractionAgentBase.inl"

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_TX__HPP__)

/*********************************************************************************************************************/
