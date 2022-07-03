function lap=U_C_lap_perrin(voltage,channel_location,n_elec)
%----------------------------------------------------------
% EEGStarlab Toolbox
% (C) Starlab S.L.
%----------------------------------------------------------
% Computes the laplacian of a given data following the algorithm developed 
% by Perrin and Pernier. See the article Perrin, F., Pernier, J., Bertrand,
% O., and Echallier, J.F. (1989) Spherical splines for scalp potential 
% and current density mapping. Electroencephalogr. Clin. Neurophysiol., 72:
% 184-187
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
% compute_lap.m from BIOSIG toolbox
%----------------------------------------------------------
% Version	Date		Author	Changes 
% v1		01/03/03    JMP     First version based on adjustBaggingFusion.m
% v2        01/12/08    IC      Update version to compute more than 1 time
%                               sample. Changed the solution of the linear system to calculate the
%                               coeficients to interpolate the data
%----------------------------------------------------------
% EX.
% 
% >> 
%--------------------------------------------------------------

pos_elec=channel_location; % (n_elec,3) array containing the position of the electrodes

degree=20;
m=2;

mat=mat_g(n_elec,m,pos_elec,degree);  % computes the matrix with the splines related to the position between electrodes 
inv_mat=inv(mat);
for t=1:length(voltage(:,1))
    new_voltage=voltage(t,:);
    new_voltage(1,n_elec+1)=0.;    %
    c=inv_mat*new_voltage'; % These are the coefficients that interpolates the data
    %c=mat/new_voltage; %this method to solve calculate a linear equation is more efficient than calculating the inverse
    for i=1:n_elec  
        lap(i,t)=compute_lap(i,c,n_elec,pos_elec,m,degree);   % compute the laplacian in the position of the electrodes
    end
end
lap=lap';

% if graph==1;
%     figure;
%     topoplot(-lap',chanlocs);   % draw the laplacian using the function topoplot available in EEGLAB.
% end