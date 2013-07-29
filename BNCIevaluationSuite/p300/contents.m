%
%  Describe all the script in the BCI-III Competition directory.
%  This is a list of script leading to the winning strategy
%
%
% createclassifier             function performing the classifier learning
%                               according to some data file and the
%                               associated model selection file
%
% filtering                     filtering and decimates each signal channel
% 
% KeepChannel                   selected the channel in xa given by the vector channel.
%
%
% MainAlgoTestRevision          testing of different data file
%                               model selection should have alreafy been
%                               performed either with modelselclassifier or
%                               with CreateDefaultCVclassifier
%
% Mainmodelsel                  Main script for modelselection and channel
%                               selection 
%
% MergeFile                     script for merging signals from different
%                               characters spelling. 
%
% modelselclassifier            function for performing channelselection
%                               and hyperparameters settings
%
% normalize                     function for normalizing channels
%
%
% preprocessingdata             script for doing the signal extration from
%                               the original datafile provided by wadsworth institute and do the
%                               filtering/decimation and saving
%
%
% TestClassifier                function for testing a set of data once a
%                               classifier has been learned
%
% TestWord                       low level function for recognizing a single character
%                                from the classification of post-stimulus signal

%--------------------------------------------------------------------------
%--------------------------------------------------------------------------
% STEP FOR PRODUCING RESULTS
%
% 1- preprocessingdata
% 2- Mainmodelsel
% 3- MainAlgotestRevision