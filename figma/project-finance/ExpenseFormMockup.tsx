import React from 'react';
import { ArrowLeft, Save, Tag, Calendar, FolderKanban, Camera, FileText, DollarSign } from 'lucide-react';

export default function ExpenseFormMockup() {
  return (
    <div className="w-[390px] h-[844px] bg-[#F5F5F5] rounded-3xl shadow-2xl overflow-hidden flex flex-col">
      {/* Status Bar */}
      <div className="h-11 bg-[#2196F3] flex items-center justify-between px-6 text-white text-xs">
        <span>9:41</span>
        <div className="flex gap-1 items-center">
          <div className="w-4 h-3 border border-white rounded-sm" />
          <div className="w-1 h-3 bg-white rounded-sm" />
        </div>
      </div>

      {/* App Bar */}
      <div className="bg-[#2196F3] px-4 py-4 text-white">
        <div className="flex items-center justify-between">
          <ArrowLeft className="w-6 h-6" />
          <span>Thêm chi phí</span>
          <Save className="w-6 h-6" />
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-auto p-4">
        {/* Type Selection */}
        <div className="bg-white rounded-xl p-4 shadow mb-4">
          <div className="text-slate-900 mb-3">Loại chi phí</div>
          <div className="flex gap-3">
            <button className="flex-1 py-3 rounded-lg bg-[#2196F3] text-white">
              Kế hoạch
            </button>
            <button className="flex-1 py-3 rounded-lg bg-slate-100 text-slate-600">
              Thực tế
            </button>
          </div>
        </div>

        {/* Basic Info */}
        <div className="bg-white rounded-xl p-4 shadow mb-4">
          <div className="space-y-4">
            {/* Category */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Danh mục *</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-center gap-3 border border-slate-200">
                <Tag className="w-5 h-5 text-slate-400" />
                <select className="flex-1 bg-transparent outline-none text-slate-900" disabled>
                  <option>Chọn danh mục</option>
                  <option>👥 Nhân sự</option>
                  <option>🛠️ Công cụ</option>
                  <option>📢 Marketing</option>
                  <option>📚 Đào tạo</option>
                  <option>🏢 Văn phòng</option>
                  <option>✈️ Đi lại</option>
                </select>
              </div>
            </div>

            {/* Description */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Mô tả *</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-start gap-3 border border-slate-200">
                <FileText className="w-5 h-5 text-slate-400 mt-0.5" />
                <input
                  type="text"
                  placeholder="Nhập mô tả chi phí"
                  className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400"
                  disabled
                />
              </div>
            </div>

            {/* Amount */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Số tiền (USD) *</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-center gap-3 border border-slate-200">
                <DollarSign className="w-5 h-5 text-slate-400" />
                <input
                  type="number"
                  placeholder="0.00"
                  className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400"
                  disabled
                />
              </div>
            </div>

            {/* Date */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Ngày *</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-center gap-3 border border-slate-200">
                <Calendar className="w-5 h-5 text-slate-400" />
                <input
                  type="text"
                  placeholder="DD/MM/YYYY"
                  className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400"
                  disabled
                />
              </div>
            </div>

            {/* Project */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Dự án</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-center gap-3 border border-slate-200">
                <FolderKanban className="w-5 h-5 text-slate-400" />
                <select className="flex-1 bg-transparent outline-none text-slate-900" disabled>
                  <option>Chọn dự án (tùy chọn)</option>
                  <option>Website Redesign</option>
                  <option>Mobile App Development</option>
                  <option>General</option>
                </select>
              </div>
            </div>
          </div>
        </div>

        {/* Receipt Upload */}
        <div className="bg-white rounded-xl p-4 shadow mb-4">
          <div className="text-slate-900 mb-3">Hóa đơn / Chứng từ</div>
          
          <div className="border-2 border-dashed border-slate-300 rounded-lg p-6 text-center">
            <div className="w-16 h-16 bg-[#2196F3]/10 rounded-full flex items-center justify-center mx-auto mb-3">
              <Camera className="w-8 h-8 text-[#2196F3]" />
            </div>
            <div className="text-sm text-slate-600 mb-1">Chụp ảnh hoặc tải lên hóa đơn</div>
            <div className="text-xs text-slate-400">JPG, PNG, PDF (Max 5MB)</div>
          </div>

          <div className="flex gap-2 mt-3">
            <button className="flex-1 bg-[#2196F3] text-white py-2 rounded-lg text-sm flex items-center justify-center gap-2">
              <Camera className="w-4 h-4" />
              Chụp ảnh
            </button>
            <button className="flex-1 bg-slate-100 text-slate-700 py-2 rounded-lg text-sm">
              Chọn file
            </button>
          </div>
        </div>

        {/* Notes */}
        <div className="bg-white rounded-xl p-4 shadow mb-4">
          <div className="text-slate-900 mb-3">Ghi chú</div>
          <textarea
            placeholder="Thêm ghi chú..."
            className="w-full bg-slate-50 rounded-lg px-4 py-3 outline-none text-sm text-slate-700 placeholder:text-slate-400 resize-none"
            rows={3}
            disabled
          />
        </div>

        {/* Status */}
        <div className="bg-white rounded-xl p-4 shadow">
          <div className="text-slate-900 mb-3">Trạng thái</div>
          <div className="flex gap-3">
            <button className="flex-1 py-2 rounded-lg bg-[#FF9800] text-white text-sm">
              Chờ duyệt
            </button>
            <button className="flex-1 py-2 rounded-lg bg-slate-100 text-slate-600 text-sm">
              Đã duyệt
            </button>
          </div>
        </div>
      </div>

      {/* Bottom Actions */}
      <div className="bg-white border-t border-slate-200 p-4 flex gap-3">
        <button className="flex-1 bg-slate-100 text-slate-700 py-3 rounded-lg">
          Hủy
        </button>
        <button className="flex-1 bg-[#2196F3] text-white py-3 rounded-lg flex items-center justify-center gap-2">
          <Save className="w-5 h-5" />
          <span>Lưu chi phí</span>
        </button>
      </div>
    </div>
  );
}
