import React from 'react';
import { ArrowLeft, Share2, Download, CheckCircle2 } from 'lucide-react';

export default function InvoiceDetailMockup() {
  const items = [
    { name: 'UI/UX Design - Phase 1', description: 'Thiết kế giao diện - Giai đoạn 1', quantity: 1, price: 5000, amount: 5000 },
    { name: 'Frontend Development', description: 'Phát triển giao diện responsive', quantity: 1, price: 2500, amount: 2500 },
  ];

  const subtotal = items.reduce((sum, item) => sum + item.amount, 0);
  const tax = subtotal * 0.1;
  const total = subtotal + tax;
  const paid = 4000;
  const remaining = total - paid;

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
          <span>Chi tiết hóa đơn</span>
          <div className="flex gap-3">
            <Share2 className="w-5 h-5" />
            <Download className="w-5 h-5" />
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-auto">
        {/* Header Card */}
        <div className="bg-white p-4 border-b border-slate-200">
          <div className="flex items-start justify-between mb-3">
            <div>
              <div className="text-xs text-slate-500 mb-1">Số hóa đơn</div>
              <div className="text-slate-900 mb-2">INV-2025-001</div>
              <div className="bg-[#2196F3]/10 text-[#2196F3] px-3 py-1 rounded text-xs inline-block">
                Đã gửi
              </div>
            </div>
            <div className="text-right">
              <div className="text-xs text-slate-500 mb-1">Tổng giá trị</div>
              <div className="text-2xl text-slate-900">${total.toLocaleString()}</div>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3 text-sm pt-3 border-t border-slate-200">
            <div>
              <div className="text-xs text-slate-500 mb-1">Ngày tạo</div>
              <div className="text-slate-700">25/10/2025</div>
            </div>
            <div>
              <div className="text-xs text-slate-500 mb-1">Hạn thanh toán</div>
              <div className="text-slate-700">30/11/2025</div>
            </div>
          </div>
        </div>

        {/* Customer Info */}
        <div className="bg-white p-4 border-b border-slate-200">
          <div className="text-xs text-slate-500 mb-2">Thông tin khách hàng</div>
          <div className="text-slate-900 mb-1">ABC Corporation</div>
          <div className="text-sm text-slate-600 mb-1">Nguyễn Văn A</div>
          <div className="text-sm text-slate-600 mb-1">nguyenvana@abc.com</div>
          <div className="text-sm text-slate-600">0901234567</div>
        </div>

        {/* Project Info */}
        <div className="bg-white p-4 border-b border-slate-200">
          <div className="text-xs text-slate-500 mb-2">Dự án</div>
          <div className="text-slate-900 mb-1">Website Redesign</div>
          <div className="text-sm text-slate-600">PRJ-2025-001</div>
        </div>

        {/* Items Table */}
        <div className="p-4">
          <div className="bg-white rounded-xl shadow overflow-hidden mb-4">
            <div className="bg-slate-50 px-4 py-3 border-b border-slate-200">
              <span className="text-slate-900">Chi tiết dịch vụ</span>
            </div>

            <div className="divide-y divide-slate-200">
              {items.map((item, index) => (
                <div key={index} className="p-4">
                  <div className="text-slate-900 mb-1">{item.name}</div>
                  <div className="text-xs text-slate-500 mb-2">{item.description}</div>
                  <div className="flex justify-between text-sm">
                    <span className="text-slate-600">
                      {item.quantity} × ${item.price.toLocaleString()}
                    </span>
                    <span className="text-slate-900">${item.amount.toLocaleString()}</span>
                  </div>
                </div>
              ))}
            </div>

            {/* Totals */}
            <div className="border-t-2 border-slate-200 p-4 space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-slate-600">Tạm tính</span>
                <span className="text-slate-900">${subtotal.toLocaleString()}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-slate-600">VAT (10%)</span>
                <span className="text-slate-900">${tax.toLocaleString()}</span>
              </div>
              <div className="flex justify-between pt-2 border-t border-slate-200">
                <span className="text-slate-900">Tổng cộng</span>
                <span className="text-[#2196F3] text-xl">${total.toLocaleString()}</span>
              </div>
            </div>
          </div>

          {/* Payment Info */}
          <div className="bg-white rounded-xl shadow p-4 mb-4">
            <div className="text-slate-900 mb-3">Thông tin thanh toán</div>
            
            <div className="space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-sm text-slate-600">Đã thanh toán</span>
                <span className="text-[#4CAF50]">${paid.toLocaleString()}</span>
              </div>

              <div className="flex justify-between items-center pb-3 border-b border-slate-200">
                <span className="text-sm text-slate-600">Còn lại</span>
                <span className="text-[#FF9800]">${remaining.toLocaleString()}</span>
              </div>

              <div className="text-xs text-slate-500 mb-2">Phương thức thanh toán</div>
              <div className="text-sm text-slate-700">Chuyển khoản ngân hàng</div>
              <div className="bg-slate-50 rounded-lg p-3 text-xs text-slate-600">
                <div>Ngân hàng: Vietcombank</div>
                <div>Số TK: 1234567890</div>
                <div>Chủ TK: CÔNG TY ABC</div>
              </div>
            </div>
          </div>

          {/* Payment History */}
          <div className="bg-white rounded-xl shadow p-4">
            <div className="text-slate-900 mb-3">Lịch sử thanh toán</div>
            
            <div className="space-y-3">
              <div className="flex items-start gap-3">
                <div className="w-8 h-8 bg-[#4CAF50]/10 rounded-full flex items-center justify-center flex-shrink-0">
                  <CheckCircle2 className="w-4 h-4 text-[#4CAF50]" />
                </div>
                <div className="flex-1">
                  <div className="flex justify-between items-start mb-1">
                    <div className="text-sm text-slate-900">Thanh toán lần 1</div>
                    <div className="text-sm text-[#4CAF50]">$4,000</div>
                  </div>
                  <div className="text-xs text-slate-500">28/10/2025 - Chuyển khoản</div>
                  <div className="text-xs text-slate-600 mt-1">Ref: TXN-001234</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Bottom Actions */}
      <div className="bg-white border-t border-slate-200 p-4 flex gap-3">
        <button className="flex-1 bg-slate-100 text-slate-700 py-3 rounded-lg">
          In hóa đơn
        </button>
        <button className="flex-1 bg-[#4CAF50] text-white py-3 rounded-lg flex items-center justify-center gap-2">
          <CheckCircle2 className="w-5 h-5" />
          <span>Xác nhận TT</span>
        </button>
      </div>
    </div>
  );
}
