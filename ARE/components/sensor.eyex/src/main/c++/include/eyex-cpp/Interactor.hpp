/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * Interactor.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTOR__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTOR__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

class Interactor :
	public InteractionObject
{
public:	
	Interactor(const std::shared_ptr<const InteractionContext>& spContext, TX_HANDLE hInteractor);
		
	std::string GetId() const;
	std::string GetParentId() const;
    std::string GetWindowId() const;

    bool GetEnabled() const;
    void SetEnabled(bool enabled);    

    bool GetDeleted() const;
    void SetDeleted(bool deleted);

    double GetZ() const;
    void SetZ(double z);
    
    void SetGazePointDataBehavior(const TX_GAZEPOINTDATAPARAMS& params);
    void SetActivatableBehavior(const TX_ACTIVATABLEPARAMS& params);
    void SetPannableBehavior(const TX_PANNABLEPARAMS& params);    
    void SetGazeAwareBehavior(const TX_GAZEAWAREPARAMS& params);    
    void SetFixationDataBehaviorParams(const TX_FIXATIONDATAPARAMS& params); 

    std::shared_ptr<InteractionBounds> GetBounds() const;
    std::vector<std::shared_ptr<InteractionBehavior>> GetBehaviors() const;
    std::shared_ptr<InteractionBounds> CreateBounds(TX_INTERACTIONBOUNDSTYPE boundsType);
    void DeleteBounds();

    std::shared_ptr<InteractionBehavior> CreateBehavior(TX_INTERACTIONBEHAVIORTYPE behaviorType);
    void DeleteBehavior(TX_INTERACTIONBEHAVIORTYPE behaviorType);
	bool TryGetBehavior(std::shared_ptr<InteractionBehavior> *pspBehavior, TX_INTERACTIONBEHAVIORTYPE behaviorType) const;

    std::shared_ptr<InteractionMask> CreateMask(TX_MASKTYPE maskType, int columnCount, int rowCount, const TX_BYTE* pData);
    void RemoveMask();
    std::shared_ptr<InteractionMask> GetMask() const;
    
    void SetMaskBounds(const TX_RECT& bounds);
    void ClearMaskBounds();
    bool TryGetMaskBounds(TX_RECT* pBounds) const;
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTOR__HPP__)

/*********************************************************************************************************************/
