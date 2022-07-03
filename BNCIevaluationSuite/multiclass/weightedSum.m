function wsumResult=weightedSum(dataVector,weightVector)
%----------------------------------------------------------
% StarEEGlab Toolbox
% (C) Starlab S.L.
%----------------------------------------------------------
% owa
%     This is a function that compute the weighted sum (WSUM) operator. 
%
%     It pressents
%     the interface in the vectorial form as the fuzzy integral. I.E. 
%     it computes the WSUM of M vectors of dimension N with respect to K
%     different sets of weights, therefore the function computes K*M WSUMs in a run.
%     Each set of weights is given in a row vector,
%     where the weighting coefficients are
%     organized as following:
%     weightVector=[coeff_1,coeff_2,....,coeff_N]
%     since N is the dimensions of the vectors to be fused. coeff_(1) applies to the
%     first vector component, coeff_2 to the second one, ..., and coeff_N to the
%     last one.
% 
%     This function is in vectorized form. Being
%     vectorized allows using it as vectorized in the GA Toolbox
%
% []
%----------------------------------------------------------
%Inputs
% dataVector:	data in matrix form of M vectors to be fused with dimensions N - matrix N x M
% weightVector: K sets of weights of N weighting coefficients - matrix K x N
%         (theoretically weighting coefficients are constrained to sum up 1)
%----------------------------------------------------------
%Outputs
% wsumResult: WSUM results organized in a matrix - matrix K x M.
%       You find in position (i,j) of this martrix the WSUM result of vector j 
%       (which was given in dataVector(:,j)) with respect to the set of weights i (whose 
%       coefficients were given in weightVector(i,:)).
%       
%----------------------------------------------------------
%Dependencies
% None
%----------------------------------------------------------
% Version	Date		Author	Changes 
% v1		16/10/10    ASF     First version based on preliminary works for oceanpal data
%----------------------------------------------------------
% EX.
%     We want to select the last component of the vector the minimum, 
%     which is equivalent to a weighted sum
%     w.r.t. a set of weights [0,0,0,...,0,1], in the first row and the
%     average (wsum w.r.t. [1,1,1,...,1] in the second row of arr
% >> arr
% 
% arr =
% 
%     0.8147    0.9134    0.2785    0.9649
%     0.9058    0.6324    0.5469    0.1576
%     0.1270    0.0975    0.9575    0.9706
% 
% >> oweights
% 
% oweights =
% 
%      0     0     1
%      1     1     1
% 
% >> weightedSum(arr,oweights)
% Weights have to sum up 1 in WSUM. Automate normalization realized.
% 
% ans =
% 
%     0.1270    0.0975    0.9575    0.9706
%     0.6158    0.5478    0.5943    0.6977
%--------------------------------------------------------------


sumWeights=sum(weightVector,2);
%constraint weights have to sum up 1
if any(abs(1.0-sumWeights)>0.005)
    disp('Weights have to sum up 1 in WSUM. Automate normalization realized.')
    weightVector=weightVector./repmat(sumWeights,1,size(weightVector,2));
end
    wsumResult=weightVector*dataVector;
end