function lap=U_C_lap(voltage,pos_elec,n_elec)
%----------------------------------------------------------
% EEGStarlab Toolbox
% (C) Starlab S.L.
%----------------------------------------------------------
% Computes the laplacian of a given epoch using a simple mean of the
% measures of the neighboring electrodes
%
%----------------------------------------------------------
%Inputs
% voltage= voltatge data that we want to analyze matrix of dimensions
%           (samples x n_elec). n_elec is equal to number of channels.
% channel_location: matrix with the cartesian coordinates of the positions of the
%                   electrodes. Dimensions n_elec X 3. It is manually
%                   computed.
% n_elec = number of electrodes;      
%----------------------------------------------------------
%Outputs
% lap = laplacian in the given electrodes. Matrix samples X n_elec
%----------------------------------------------------------
%Dependencies
% 
%----------------------------------------------------------
% Version	Date		Author	Changes 
% v1		01/03/03    JMP     First version
% v2        01/12/08    IC      Update version to compute more than 1 time
%                               sample. Changed the solution of the linear system to calculate the
%                               coeficients to interpolate the data
%----------------------------------------------------------
% EX.
% 
% >> 
%--------------------------------------------------------------


%%%find distances between electrodesto select neighboring channels.%%%
for i=1:length(pos_elec(:,1))
    for j=1:length(pos_elec(:,1))
        d(i,j)=sqrt((pos_elec(i,1)-pos_elec(j,1))^2+(pos_elec(i,2)-pos_elec(j,2))^2);
    end
end
for t=1:length(voltage(:,1))%%for all time samples
    for i=1:length(pos_elec(:,1))%%%we make an array with the electreodes that are arround
        neigh=find(d(i,:)==1);
        lap(t,i)=voltage(t,i)-((sum(voltage(i,neigh)))/length(neigh));
    end
end

%  if graph==1;
%      figure;
%      topoplot(-lap',chanlocs);   % draw the laplacian using the function topoplot available in EEGLAB.
%  end